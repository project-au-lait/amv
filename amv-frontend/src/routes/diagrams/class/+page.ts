import type { TypeSearchCriteriaModel, TypeSearchResultModel } from '$lib/arch/api/Api';
import ApiHandler from '$lib/arch/api/ApiHandler';
import CriteriaUtils from '$lib/arch/search/CriteriaUtils';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    ...CriteriaUtils.decode(url)
  } as TypeSearchCriteriaModel;

  // #14629:BEGIN
  // 初期起動時は空とする
  let result: TypeSearchResultModel = { list: [] };
  let classDiagram = '';

  // テキスト入力値を取得
  const hasCriteria = criteria.text && criteria.text.trim().length >= 2;

  // criteriaが2文字以上入力時のみ検索処理を実行する
  if (hasCriteria) {
    result = (await ApiHandler.handle<TypeSearchResultModel>(fetch, (api) =>
      api.type.search(criteria)
    )) || { list: [] };
    if (result.list && result.list.length === 1) {
      classDiagram =
        (await ApiHandler.handle<string>(fetch, (api) =>
          api.diagrams.classDiagram({
            qualifiedName: result!.list![0].qualifiedName!
          })
        )) || '';
    }
  }
  // #14629:END

  return {
    criteria,
    result,
    classDiagram
  };
};
