import type {
  TypeSearchCriteriaModel,
  TypeSearchResultModel,
  DiagramModel,
  DiagramCriteriaModel
} from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export type CriteriaModel = {
  typeSearchCriteria: TypeSearchCriteriaModel;
  diagramCriteria: DiagramCriteriaModel;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    typeSearchCriteria: {},
    diagramCriteria: {
      qualifiedSignature: '',
      depth: 10
    },
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const typeResult = await searchTypes(criteria.typeSearchCriteria, fetch);

  criteria.diagramCriteria.qualifiedSignature = await getQualifiedName(criteria, typeResult);

  const classDiagram = await getClassDiagram(criteria.diagramCriteria, fetch);

  return {
    criteria,
    typeResult,
    classDiagram
  };
};

async function getQualifiedName(criteria: CriteriaModel, typeResult: TypeSearchResultModel) {
  if (typeResult.list?.length === 1) {
    return typeResult.list[0].qualifiedName ?? '';
  }

  return (
    typeResult.list?.find((type) => type.qualifiedName === criteria.typeSearchCriteria.text)
      ?.qualifiedName ?? ''
  );
}

async function searchTypes(
  typeSearchCriteria: TypeSearchCriteriaModel,
  fetch: typeof window.fetch
) {
  if (!typeSearchCriteria?.text || typeSearchCriteria?.text.length < 2) {
    return {} as TypeSearchResultModel;
  }

  return (
    (await ApiHandler.handle<TypeSearchResultModel>(fetch, (api) =>
      api.type.search(typeSearchCriteria)
    )) || {
      list: []
    }
  );
}

async function getClassDiagram(criteria: DiagramCriteriaModel, fetch: typeof window.fetch) {
  if (!criteria.qualifiedSignature) {
    return {} as DiagramModel;
  }

  return await ApiHandler.handle<DiagramModel>(
    fetch,
    (api) => api.diagrams.classDiagram(criteria) || ({} as DiagramModel)
  );
}
