import ApiHandler from '$lib/arch/api/ApiHandler';
import type {
  InteractionDocumentCriteriaModel,
  MethodSearchCriteriaModel,
  MethodSearchResultModel,
  CallTreeModel
} from '$lib/arch/api/Api';
import type { PageLoad } from './$types';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';

export type CriteriaModel = {
  methodCriteria: MethodSearchCriteriaModel;
  documentCriteria: InteractionDocumentCriteriaModel;
  init: boolean;
};

export type CallTreeCriteriaModel = {
  signaturePattern?: string;
  callTreeRequired?: boolean;
  calledTreeRequired?: boolean;
  render?: string;
  /** @format int32 */
  limit?: number;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const decoded = CriteriaUtils.decode(url);
  const criteria = {
    methodCriteria: {
      text: decoded.signaturePattern ?? decoded.methodCriteria?.text
    },
    documentCriteria: {
      participableStereotypes: []
    },
    init: true,
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const methods = await searchMethods(criteria.methodCriteria, fetch);

  criteria.documentCriteria.qualifiedSignature = methods.list?.length === 1 ? methods.list[0].qualifiedSignature! : "";

  let callTrees: CallTreeModel[] = [];

  if (Array.isArray(methods.list) && methods.list.length === 1) {
    callTrees = await getCallTree(url, methods.list[0].qualifiedSignature);
  }

  return {
    criteria,
    methods,
    showExternalPackage: false,
    packageLevel: 3,
    callTrees
  };
};

async function searchMethods(criteria: MethodSearchCriteriaModel, fetch: typeof window.fetch) {
  if (!criteria.text || criteria.text.length < 2) {
    return {} as MethodSearchResultModel;
  }

  return (
    (await ApiHandler.handle<MethodSearchResultModel>(fetch, (api) => api.methods.search(criteria))) ||
    ({} as MethodSearchResultModel)
  );
}

async function getCallTree(url: URL, signaturePattern: string | undefined) {
  const criteria = {
    callTreeRequired: true,
    calledTreeRequired: true,
    render: "HTML",
    limit: 10,
    signaturePattern: signaturePattern,
    ...CriteriaUtils.decode(url)
  } as CallTreeCriteriaModel;

  return (await ApiHandler.handle<CallTreeModel[]>(fetch, (api) => api.diagrams.callTree(criteria))) || [];
}
