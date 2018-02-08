package ru.masterdm.spo.dashboard.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ru.masterdm.spo.dashboard.model.metadata.GridMetadataStateLimitFactory;
import ru.masterdm.spo.dashboard.model.metadata.GridMetadataStateOtherFactory;
import ru.masterdm.spo.dashboard.model.metadata.GridMetadataTopLimitFactory;
import ru.masterdm.spo.dashboard.model.metadata.GridMetadataTopOtherFactory;

/**
 * Все известные
 * @author pmasalov
 */
public enum EModel {
    COLUMN_CHART_MODEL(ColumnChartModelFactory.class),
    PIE_CHART_MODEL(PieChartModelFactory.class),
    LINE_CHART_MODEL(LineChartModelFactory.class),
    FULL_DEPARTMENT_MODEL(FullDepartmentModelFactory.class),
    FILTERED_DEPARTMENT_MODEL(FilteredDepartmentModelFactory.class),
    SUMMARY_DATA_CREATOR_LIMIT(SummaryDataCreatorLimit.class),
    SUMMARY_DATA_CREATOR_OTHER(SummaryDataCreatorOther.class),
    GRID_METADATA_STATE_LIMIT_FACTORY(GridMetadataStateLimitFactory.class),
    GRID_METADATA_STATE_OTHER_FACTORY(GridMetadataStateOtherFactory.class),
    GRID_METADATA_TOP_LIMIT_FACTORY(GridMetadataTopLimitFactory.class),
    GRID_METADATA_TOP_OTHER_FACTORY(GridMetadataTopOtherFactory.class);

    private Class<? extends ModelFactoryAbstract> modelFactoryClass;

    EModel(Class<? extends ModelFactoryAbstract> modelFactoryClass) {
        this.modelFactoryClass = modelFactoryClass;
    }

    public <T> ModelFactory createModelFactory(T param) {
        try {
            Constructor<T> cons = (Constructor<T>) modelFactoryClass.getConstructor(param.getClass());
            ModelFactory f = (ModelFactory) cons.newInstance(param);
            return f;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}