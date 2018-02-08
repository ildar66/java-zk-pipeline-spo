package org.uit.director.managers;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 11.03.2005
 * Time: 8:38:17
 * To change this template use File | Settings | File Templates.
 */
/*public class StatisticManager {

    private Object resultStat;

    private StatSearchAtribute pageDataSearchForAtributes;
    private StatSearchUserWorks pageDataUsersWorks;
    private StatSearchProcessStatistic pageDataStatistics;
    private StatSearchUserWeight pageDataUserWeight;
    private StatExpiredProcesses pageExpiredProcesses;
    private StatDispersProcesses dispersProcesses;
    private List buildSeries;
    private CommonStatProc commonStatProc;
    private StatSearchProcessStatistic pageDataMagazine;


    public CommonStatProc getCommonStatProc() {
        return commonStatProc;
    }

    public void setCommonStatProc(CommonStatProc commonStatProc) {
        this.commonStatProc = commonStatProc;
    }


    public void initBuildSeries(DBWoodenDirector director, String region, String dbegin, String dend) {
        buildSeries = new ArrayList();
        List stat = director.getStatData(region, dbegin, dend);
        int[][] statInt = null;
        int size = stat.size();
        statInt = new int[size][2];

        for (int i = 0; i < size; i++) {
            Map db = (Map) stat.get(i);

            statInt[i][0] = Integer.parseInt((String) db.get("O_DESCRIPTION"));
            statInt[i][1] = Integer.parseInt((String) db.get("O_NUMDAYS"));

        }
        int statINtRole[][] = getStatForRole(statInt, 1);
        buildSeries.add(new BuildSeries(statINtRole, 1, region, dbegin, dend));

        statINtRole = getStatForRole(statInt, 2);
        buildSeries.add(new BuildSeries(statINtRole, 2, region, dbegin, dend));

        statINtRole = getStatForRole(statInt, 3);
        buildSeries.add(new BuildSeries(statINtRole, 3, region, dbegin, dend));

        statINtRole = getStatForRole(statInt, 4);
        buildSeries.add(new BuildSeries(statINtRole, 4, region, dbegin, dend));

        statINtRole = getStatForRole(statInt, 5);
        buildSeries.add(new BuildSeries(statINtRole, 5, region, dbegin, dend));


    }

    private int[][] getStatForRole(int[][] statInt, int role) {

        int countForRole = 0;

        for (int i = 0; i < statInt.length; i++) {
            if (statInt[i][0] == role) countForRole++;
        }

        int[][] res = new int[countForRole][2];

        int count = 0;
        for (int i = 0; i < statInt.length; i++) {
            if (statInt[i][0] == role) {
                res[count][0] = statInt[i][0];
                res[count++][1] = statInt[i][1];

            }
        }

        return res;

    }

    public List getBuildSeries() {
        return buildSeries;
    }

    public void setBuildSeries(List buildSeries) {
        this.buildSeries = buildSeries;
    }

    public Object getResultStat() {
        return resultStat;
    }

    public void setResultStat(Object resultStat) {
        this.resultStat = resultStat;
    }

    public StatSearchAtribute getPageDataSearchForAtributes() {
        return pageDataSearchForAtributes;
    }

    public void setPageDataSearchForAtributes(StatSearchAtribute pageDataSearchForAtributes) {
        this.pageDataSearchForAtributes = pageDataSearchForAtributes;
    }

    public StatSearchUserWorks getPageDataUsersWorks() {
        return pageDataUsersWorks;
    }

    public void setPageDataUsersWorks(StatSearchUserWorks pageDataUsersWorks) {
        this.pageDataUsersWorks = pageDataUsersWorks;
    }

    public StatSearchProcessStatistic getPageDataStatistics() {
        return pageDataStatistics;
    }

    public void setPageDataStatistics(StatSearchProcessStatistic pageDataStatistics) {
        this.pageDataStatistics = pageDataStatistics;
    }

    public List getProcessFindAtr(String proc_manager,
                                  String search, boolean isActive) {
        List res = null;

        try {

            res = new ArrayList();
            res.add(new ProcessSearchData("", "", "", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private String isValueOnAtributs(Map nm, String search) {
        List delim = new ArrayList();
        StringTokenizer token = new StringTokenizer(search, " ");

        int counttok = token.countTokens();
        for (int tok = 0; tok < counttok; tok++) {
            delim.add(token.nextToken().toLowerCase().trim());
        }

        String result = "";
        boolean flag;   //не используется пока. Может необходим в случае поиска хотя бы одново слова в атрибктах процесса
        int countSearch = 0; //количество найденных элементов
        flag = false;
        ArrayList keyMass = new ArrayList(nm.keySet());
        int countMass = keyMass.size();
        for (int i = 0; i < delim.size(); i++) {

            for (int j = 0; j < countMass; j++) {

                String valuetmp = String.valueOf(nm.get(keyMass.get(j)));
                String value = valuetmp.toLowerCase().trim();

                if (value.lastIndexOf(String.valueOf(delim.get(i))) != -1) {
                    countSearch++;
                    flag = true;
                    result += valuetmp + " ";
                }
            }


        }

        if (countSearch < delim.size()) return null;

        return result;

    }


    public void setPageDataUsersWeight(StatSearchUserWeight weight) {
        pageDataUserWeight = weight;
    }

    public void setPageDispersProcesses(StatDispersProcesses dispersProcesses) {
        this.dispersProcesses = dispersProcesses;

    }

    public StatDispersProcesses getDispersProcesses() {
        return dispersProcesses;
    }



    public class ProcessSearchData {
        public String processKey;
        public String processName;
        public String idProcess;
        public String searchAtribut;

        public ProcessSearchData(String processKey, String processName, String idProcess, String searchAtribut) {
            this.processKey = processKey;
            this.processName = processName;
            this.idProcess = idProcess;
            this.searchAtribut = searchAtribut;
        }

    }

    public class ViewAtributes {
        public String nameAtribute;
        public String keyAtribute;
        public String valueAtribute;

        public ViewAtributes(String nameAtribute, String keyAtribute, String valueAtribute) {
            this.nameAtribute = nameAtribute;
            this.keyAtribute = keyAtribute;
            this.valueAtribute = valueAtribute;
        }

    }



    public StatSearchUserWeight getPageDataUserWeight() {
        return pageDataUserWeight;
    }

    public StatExpiredProcesses getPageExpiredProcesses() {
        return pageExpiredProcesses;
    }

    public void setPageExpiredProcesses(StatExpiredProcesses pageExpiredProcesses) {
        this.pageExpiredProcesses = pageExpiredProcesses;
    }

    public void setPageDataMagazine(StatSearchProcessStatistic pageDataMagazine) {
        this.pageDataMagazine = pageDataMagazine;
    }

    public StatSearchProcessStatistic getPageDataMagazine() {
        return pageDataMagazine;
    }


}*/
