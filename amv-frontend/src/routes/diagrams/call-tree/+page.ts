import type { CallTreeCriteriaModel, CallTreeResponseModel } from "$lib/arch/api/Api";
import ApiHandler from "$lib/arch/api/ApiHandler";
import CriteriaUtils from "$lib/arch/search/CriteriaUtils";
import type { PageLoad } from "./$types";

export const load: PageLoad = async ({ fetch, url }) => {
  const criteria = {
    callTreeRequired: true,
    calledTreeRequired: true,
    render: "HTML",
    limit: 10,
    ...CriteriaUtils.decode(url)
  } as CallTreeCriteriaModel;

  const open = CriteriaUtils.decodeParam<boolean>(url, "open") ?? false;

  let callTrees: CallTreeResponseModel = {
    count: 0,
    results: []
  };

  const hasCriteria = criteria.signaturePattern && criteria.signaturePattern.trim().length >= 2;

  if (hasCriteria) {
    callTrees = (await ApiHandler.handle<CallTreeResponseModel>(fetch, (api) => api.diagrams.callTree(criteria))) ?? {
      count: 0,
      results: []
    };
  }

  return {
    title: "Call Tree",
    criteria,
    open,
    callTrees,
    showExternalPackage: false,
    packageLevel: 3
  };
};
