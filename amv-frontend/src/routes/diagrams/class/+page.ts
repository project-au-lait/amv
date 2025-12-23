import type {
  TypeSearchCriteriaModel,
  TypeSearchResultModel,
  DiagramSearchCriteriaMode
} from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';
import type { DiagramModel } from '$lib/arch/api/Api';

export type CriteriaModel = {
  typeSearchCriteriaModel: TypeSearchCriteriaModel;
  diagramSearchCriteriaMode: DiagramSearchCriteriaMode;
};

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    typeSearchCriteriaModel: {},
    diagramSearchCriteriaMode: {
      qualifiedName: '',
      depth: 10
    },
    ...CriteriaUtils.decode(url)
  } as CriteriaModel;

  const typeResult = await typeSearchClass(criteria.typeSearchCriteriaModel, fetch);

  criteria.diagramSearchCriteriaMode.qualifiedName =
    criteria.typeSearchCriteriaModel.text && typeResult.list
      ? (typeResult.list.find(
          (type) => type.qualifiedName === criteria.typeSearchCriteriaModel.text
        )?.qualifiedName ?? '')
      : '';

  const classDiagram = await getClassDiagram(criteria.diagramSearchCriteriaMode, fetch);

  return {
    criteria,
    typeResult,
    classDiagram
  };
};

async function typeSearchClass(
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

async function getClassDiagram(
  diagramSearchCriteria: DiagramSearchCriteriaMode,
  fetch: typeof window.fetch
) {
  if (!diagramSearchCriteria.qualifiedName || diagramSearchCriteria.qualifiedName.length < 2) {
    return {} as DiagramModel;
  }

  return await ApiHandler.handle<DiagramModel>(
    fetch,
    (api) =>
      api.diagrams.classDiagram({
        qualifiedName: diagramSearchCriteria.qualifiedName,
        depth: diagramSearchCriteria.depth
      }) || ({} as DiagramModel)
  );
}
