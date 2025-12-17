import ApiHandler from '$lib/arch/api/ApiHandler';
import type {
  MethodSearchCriteriaModel,
  MethodSearchResultModel,
  CallTreeModel,
  CallTreeCriteriaModel
} from '$lib/arch/api/Api';
import type { PageLoad } from './$types';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

export type CriteriaModel = {
  methodCriteria: MethodSearchCriteriaModel;
  init: boolean;
  signaturePattern: string;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    methodCriteria: {},
    documentCriteria: {
      participableStereotypes: []
    },
    init: true,
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const methods = await searchMethods(criteria, fetch);

  let callTrees: CallTreeModel[] = [];

  if (Array.isArray(methods.list) && methods.list.length === 1) {
    callTrees = await getCallTree(methods.list[0].qualifiedSignature);
  }

  return {
    criteria,
    methods,
    showExternalPackage: false,
    packageLevel: 3,
    callTrees
  };
};

async function searchMethods(criteria: CriteriaModel, fetch: typeof window.fetch) {
  if (
    (criteria.methodCriteria?.text ?? '').length < 2 &&
    (criteria.signaturePattern ?? '').length < 2
  ) {
    return {} as MethodSearchResultModel;
  }

  if (criteria.signaturePattern && !criteria.methodCriteria?.text) {
    criteria.methodCriteria = {
      ...criteria.methodCriteria,
      text: criteria.signaturePattern
    };
  }

  return (
    (await ApiHandler.handle<MethodSearchResultModel>(fetch, (api) =>
      api.methods.search(criteria.methodCriteria)
    )) || ({} as MethodSearchResultModel)
  );
}

async function getCallTree(signaturePattern: string | undefined) {
  const criteria = {
    callTreeRequired: true,
    calledTreeRequired: true,
    render: 'HTML',
    limit: 10,
    signaturePattern: signaturePattern
  } as CallTreeCriteriaModel;

  return (
    (await ApiHandler.handle<CallTreeModel[]>(fetch, (api) => api.diagrams.callTree(criteria))) ||
    []
  );
}
