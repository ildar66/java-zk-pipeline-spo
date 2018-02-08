package org.uit.director.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.uit.director.contexts.WorkflowSessionContext;

import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.util.Config;

import com.vtb.domain.ProcessSearchParam;

public class ProcessList implements Serializable {

	private static final long serialVersionUID = 1L;
	 private static final Logger LOGGER = Logger.getLogger(ProcessList.class.getName());

	private List<ProcessInfo> tableProcessList;

	private WorkflowSessionContext wsc;

	private Long idUser;

	/*текущая страница процессов*/
	private int currPage;

	/*общее количество страниц процессов*/
	private int allPages;

	/*количество процессов на странице*/
	private int processesOnPage;

	private boolean[] isLoadPageMas;

	public ProcessList() {
		tableProcessList = new ArrayList<ProcessInfo>();
	}

	public void init(WorkflowSessionContext wsc) {
		this.wsc = wsc;
		idUser = wsc.getIdUser();
	}

	public String execute(Integer idDepartment, ProcessSearchParam processSearchParam) {
		long tstart=System.currentTimeMillis();
		String result = "ok";
		try {

		    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			LOGGER.info("*** wsc.getDbManager().getDbFlexDirector() "+(System.currentTimeMillis()-tstart));
			long current=System.currentTimeMillis();
			List<Long> idProcessList = pupFacadeLocal.getProcessList(idUser, idDepartment, processSearchParam);
			LOGGER.info("*** getProcessList "+(System.currentTimeMillis()-current));

			currPage = 0;
			
			int allProcesses = idProcessList.size();

			processesOnPage = Integer.parseInt(Config.getProperty("PROCESSES_ON_PAGE"));

			allPages = round(allProcesses);

			isLoadPageMas = new boolean[allPages];
			isLoadPageMas[0] = true;

			current=System.currentTimeMillis();
			for (int i = 0; i < allProcesses; i++) {
				Long idProcess = idProcessList.get(i);
				ProcessInfo processInfo = new ProcessInfo();
				processInfo.init(wsc, idProcess, idUser, false);
				tableProcessList.add(processInfo);
				if (i < processesOnPage) {
					String res = addProcessInfo(i);
					if (res.equalsIgnoreCase("Error")) {
						return "Error";
					}
				}
			}
			LOGGER.info("*** ProcessList.execute big cycle "+(System.currentTimeMillis()-current));

		} catch (Exception e) {
			e.printStackTrace();
			result = "Error";
		}
		LOGGER.info("*** ProcessList.execute "+(System.currentTimeMillis()-tstart));

		return result;
	}

	private int round(int allProcesses) {
		float res = (float) allProcesses / processesOnPage;
		int intMin = (int) Math.floor(res);
		if (res - intMin > 0 || allProcesses == 0) {
			intMin++;
		}

		return intMin;
	}
	
	public String addProcessInfo(int idx){
		String result = "error";
		try {
			
			/*ProcessInfo processInfo = tableProcessList.get(idx);
			
			if (processInfo.getIdTypeProcess() == null){
				String res = processInfo.execute();				
				if (!res.equalsIgnoreCase("Error")) {*/
					result = "ok";
				/*}
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;
	}

	public String addProcessInfo(Long idProcess){
		String result = "error";
		try {
			
			ProcessInfo processInfo = findProcessInfo(idProcess);
			
			if (processInfo == null){
				processInfo = new ProcessInfo();
				processInfo.init(wsc, idProcess, idUser, false);
				String res = processInfo.execute();				
				if (!res.equalsIgnoreCase("Error")) {
					tableProcessList.add(processInfo);
					result = "ok";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;
	}
	
	public void deleteProcessInfo(long idProcess) {

		int col = tableProcessList.size();
		for (int i = 0; i < col; i++) {
			if ( (tableProcessList.get(i)).getIdProcess().longValue() == idProcess) {
				tableProcessList.remove(i);
				return;
			}

		}
	}
	
	public ProcessInfo findProcessInfo(Long idProcess) {

		int col = tableProcessList.size();
		for (int i = 0; i < col; i++) {
			ProcessInfo process = tableProcessList.get(i);
			Long idPr = process.getIdProcess();

			if (idPr.equals(idProcess)) {
				return process;
			}

		}
		return null;

	}

	public void nextPage() {
		if (currPage + 1 > allPages) {
			return;
		}
		currPage++;
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	public void previosPage() {
		if (currPage < 1) {
			return;
		}
		currPage--;
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	public void setPage(String navigation) {

		if(navigation.equals(""))navigation="0";
		currPage = Integer.parseInt(navigation);
		if (currPage < 0 || currPage > allPages) {
			return;
		}
		loadPage();
		isLoadPageMas[currPage] = true;
	}

	private void loadPage() {

		int allProcesses = tableProcessList.size();
		for (int i = currPage * processesOnPage; i < (currPage + 1) * processesOnPage; i++) {
			if (i == allProcesses) {
				break;
			}
			addProcessInfo(i);
		}
	}
	
	public boolean isLoadAllProcessesList() {
		for (boolean b : isLoadPageMas) {
			if (!b) {
				return false;
			}
		}

		return true;

	}

	public int rightBoundPage() {

		int res = (currPage + 1) * processesOnPage;
		if (res > tableProcessList.size()) {
			res = tableProcessList.size();
		}
		return res;
	}

	public int leftBoundPage() {
		return currPage * processesOnPage;
	}

	public String getNavigation(String urlappend) {
		StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"paging\"><input name=\"navigation\" id=\"navigation\" type=\"hidden\">");
        sb.append("Всего заявок: ").append(tableProcessList.size()).append("; страниц: ").append(allPages);
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        String hrefPageFormat="<a onClick=\"$('#navigation').val('%s');$('#processGrid').submit()\" href = \"#\" >%s</a> &nbsp";
        
        if (currPage != 0) {
            sb.append(String.format(hrefPageFormat, "0","Первая"));
            sb.append(String.format(hrefPageFormat, currPage-1,"Назад"));
        }
		if (allPages > 1) {
			int leftB = leftNavigation();
			int rightB = rightNavigation();
			for (int i = leftB; i < rightB; i++) {
				if (i == currPage) {
					sb.append("<span class=\"selected\">");
					sb.append(i + 1);
					sb.append("</span>&nbsp;");
				} else {
				    sb.append(String.format(hrefPageFormat, i,i+1));
				}
			}
		}
        if (currPage != allPages - 1) {
            sb.append(String.format(hrefPageFormat, currPage+1,"Вперед"));
            sb.append(String.format(hrefPageFormat, allPages - 1,"Последняя"));
        }
		sb.append("</div>");
		return sb.toString();
	}
	

	private int leftNavigation() {
		for (int i = currPage; i >= 0; i--) {
			if (((i + 1) % 10) == 0) {
				return i;
			}
		}
		return 0;

	}

	private int rightNavigation() {

		int start = currPage;
		if (((currPage + 1) % 10) == 0 && currPage != 0) {
			start++;
		}

		for (int i = start; i < allPages; i++) {
			if (((i + 1) % 10) == 0 && i != 0) {
				return i;
			}
		}
		return allPages;
	}

	public List<ProcessInfo> getTableProcessList() {
		return tableProcessList;
	}
	
}
