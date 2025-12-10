import type { TypeSearchCriteriaModel, TypeSearchResultModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    ...CriteriaUtils.decode(url)
  } as TypeSearchCriteriaModel;

  const fromLink = url.searchParams.get('fromLink') === '1';

  let result: TypeSearchResultModel = { list: [] };
  let classDiagram = '';

  const hasCriteria = criteria.text && criteria.text.trim().length >= 2;

  if (hasCriteria) {
    result = (await ApiHandler.handle<TypeSearchResultModel>(fetch, (api) =>
      api.type.search(criteria)
    )) || { list: [] };

    if ((result.list && result.list.length === 1) || fromLink) {
      const target = fromLink && criteria.text ? criteria.text : result.list![0].qualifiedName!;

      classDiagram =
        (await ApiHandler.handle<string>(fetch, (api) =>
          api.diagrams.classDiagram({
            qualifiedName: target
          })
        )) || '';
    }
  }

  return {
    criteria,
    result,
    classDiagram
  };
};
