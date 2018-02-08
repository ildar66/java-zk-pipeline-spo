package org.uit.director.db.dbobjects;

import java.io.Serializable;


public class Cnst implements Serializable {


    public static class TBLS {
        public static final int stages = 0;
        public static final int typeProcesses = 1;
        public static final int roles = 2;
        public static final int statusProcesses = 3;
        public static final int variables = 4;
    }

    public static class TStages {

        public static final String id = "id";
        public static final String name = "name";
        public static final String description = "description";
        public static final String limitDay = "limitDay";
        public static final String typeLimitDay = "typeLimitDay";
        public static final String attentionDay = "attentionDay";
        public static final String classEntry = "classEntry";
        public static final String classExit = "classExit";
        public static final String idTypeProcess = "idTypeProcess";

    }

    public static class TTypeProc {

        public static final String id = "id";
        public static final String name = "name";
        public static final String limitDay = "limitDay";
        public static final String schema = "schema";
        public static final String idGroup = "idGroup";

    }

    public static class TProcPar {

        public static final String id = "id";
        public static final String name = "name";
        public static final String value = "value";

    }

    public static class TRoles {

        public static final String id = "id";
        public static final String name = "name";
        public static final String idTypeProcess = "idTypeProcess";

    }

    public static class TStatProc {

        public static final String id = "id";
        public static final String name = "name";

    }

    public static class TUserInRole {

        public static final String id = "id";
        public static final String name = "name";

    }
    
    public static class TStagesInRole {

        public static final String idRole = "id";
        public static final String idStage = "idStage";

    }

    public static class TVar {       
		
		public static final String id = "id";
        public static final String name = "name";
        public static final String description = "description";
        public static final String typeVar = "typeVar";
        public static final String addition = "addition";
        public static final String isId = "isId";
        public static final String isMain = "isMain";
        public static final String orderVar = "orderVar";
        public static final String id_ds = "id_ds";
    }


}
