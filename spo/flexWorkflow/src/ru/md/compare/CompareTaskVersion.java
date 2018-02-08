package ru.md.compare;

import java.util.ArrayList;
import java.util.List;

import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

/**
 * @author rislamov
 */
public class CompareTaskVersion {

	public static List<CompareSublimitsInfo> getCompareSublimitsInfos(String parentIds, String objType)
			throws Exception {
		TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(
				TaskFacadeLocal.class);
		List<TaskJPA> parents = new ArrayList<TaskJPA>();
		List<CompareSublimitsInfo> res = new ArrayList<CompareSublimitsInfo>();
		for (String id : parentIds.split("\\|")) {
			Long parentId = Long.parseLong(id);
			if (parentId > 0)
				parents.add(taskFacade.getTask(parentId));
			else
				parents.add(new TaskJPA());
		}
		res.add(new CompareSublimitsInfo(parentIds, parents.get(0).getNumberDisplay(), objType));
		if (objType.equals("limit")) {
			List<String> passedIds = new ArrayList<String>();
			getCompareSublimitsInfos(parents, res, passedIds);
		}
		return res;
	}

	private static void getCompareSublimitsInfos(List<TaskJPA> parents,
			List<CompareSublimitsInfo> result, List<String> passedIds) throws Exception {
		for (int i = 0; i < parents.size(); i++) {
			if (parents.get(i) != null && parents.get(i).getChilds() != null) {
				for (int j = 0; j < parents.get(i).getChilds().size(); j++) {
					TaskJPA currSublimit = parents.get(i).getChilds().get(j);
					if (!passedIds.contains(currSublimit.getNumberDisplayWithRoot()) 
							&& !currSublimit.isDeleted() && currSublimit.isSublimit()) {
						List<TaskJPA> newParents = new ArrayList<TaskJPA>();
						String subIds = "";
						for (int _i = 0; _i < parents.size(); _i++) {
							TaskJPA newChild = new TaskJPA();
							String subId = "0";
							if (_i >= i && parents.get(_i).getChilds() != null) {
								for (int _j = 0; _j < parents.get(_i).getChilds().size(); _j++) {
									TaskJPA _currSublimit = parents.get(_i).getChilds().get(_j);
									if (currSublimit.getNumberDisplayWithRoot().equals(
											_currSublimit.getNumberDisplayWithRoot())  && !_currSublimit.isDeleted()) {
										newChild = _currSublimit;
										subId = _currSublimit.getId().toString();
										_j = parents.get(_i).getChilds().size();
									}
								}
							}
							subIds += subId + (_i + 1 < parents.size() ? "|" : "");
							newParents.add(newChild);
						}
						result.add(new CompareSublimitsInfo(subIds, currSublimit.getNumberDisplay(),
								"sublimit"));
						passedIds.add(currSublimit.getNumberDisplayWithRoot());
						getCompareSublimitsInfos(newParents, result, passedIds);
					}
				}
			}
		}
	}
}