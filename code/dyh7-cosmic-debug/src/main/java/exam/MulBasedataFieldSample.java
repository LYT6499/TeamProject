package exam;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.property.BasedataProp;
import kd.bos.entity.property.MulBasedataProp;
import kd.bos.entity.property.PKFieldProp;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.form.control.events.ItemClickEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 单据界面插件
 */
public class MulBasedataFieldSample extends AbstractBillPlugIn implements Plugin {

    // 多选基础资料字段的标识（选择题目用）
    private final static String KEY_MULBASEDATAFIELD = "dyh7_mulbasedatafield";

    private final static String KEY_TABLE = "dyh7_entryentity";
    // 表格列标识
    private final static String COL_QUESTION_ID = "dyh7_txtc1";
    private final static String COL_QUESTION_CONTENT = "dyh7_txtc2";
    private final static String COL_QUESTION_TYPE = "dyh7_txtc3";
    private final static String COL_OPTION_A = "dyh7_txtc4";
    private final static String COL_OPTION_B = "dyh7_txtc5";
    private final static String COL_OPTION_C = "dyh7_txtc6";
    private final static String COL_OPTION_D = "dyh7_txtc7";
    private final static String COL_OPTION_E = "dyh7_txtc8";
    private final static String COL_Score_value = "dyh7_txtc9";
    private final static String COL_single_selection = "dyh7_txtc10";

    // 题目类型映射（值 -> 标题）
    private static final Map<String, String> QUESTION_TYPE_MAP = new HashMap<>();
    static {
        QUESTION_TYPE_MAP.put("1", "单选题");
        QUESTION_TYPE_MAP.put("2", "多选题");
        QUESTION_TYPE_MAP.put("3", "判断题");
        QUESTION_TYPE_MAP.put("4", "简答题");
        QUESTION_TYPE_MAP.put("5", "计算题");
        QUESTION_TYPE_MAP.put("6", "填空题");
        QUESTION_TYPE_MAP.put("7", "综合题");
    }

    // 是否显示映射（值 -> 标题）
    private static final Map<String, String> YES_NO_MAP = new HashMap<>();
    static {
        YES_NO_MAP.put("1", "是");
        YES_NO_MAP.put("2", "否");
    }
    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        String fieldKey = e.getProperty().getName();
        if (StringUtils.equals(KEY_MULBASEDATAFIELD, fieldKey)){
            // TODO 在此添加业务逻辑
            // 获取多选基础资料字段的值（选中的题目主键集合）
            DynamicObjectCollection selectedQuestions =
                    (DynamicObjectCollection) this.getModel().getValue(KEY_MULBASEDATAFIELD);
            this.getView().showMessage("题目的selectedQuestions.size():"+selectedQuestions.size());

            // 遍历选中的题目，添加到表格中
            //if (selectedQuestions != null && !selectedQuestions.isEmpty()) {
            for (DynamicObject questionRef : selectedQuestions) {
                // 获取题目的主键
                // 根据主键加载完整的题目数据
                DynamicObject fullQuestion = questionRef.getDynamicObject("fbasedataid");

                // 调试：显示加载的题目内容
                //this.getView().showMessage("加载题目: " + fullQuestion.getString("name"));
                //System.out.println("加载题目: " + fullQuestion.getString("name"));

                // 创建表格行
                this.getModel().createNewEntryRow(KEY_TABLE);
                int rowIndex = this.getModel().getEntryRowCount(KEY_TABLE) - 1;

                // 题目类型映射
                String questionTypeValue = fullQuestion.getString("dyh7_topic_type");
                String questionTypeTitle = QUESTION_TYPE_MAP.getOrDefault(questionTypeValue, "未知类型");

                // 设置各列的值（从完整题目数据中获取）
                this.getModel().setValue(COL_QUESTION_ID, fullQuestion.getPkValue(),rowIndex);
                this.getModel().setValue(COL_QUESTION_CONTENT, fullQuestion.getString("name"),rowIndex);
//                this.getModel().setValue(COL_QUESTION_TYPE, fullQuestion.getString("dyh7_topic_type"),rowIndex);
                this.getModel().setValue(COL_QUESTION_TYPE, questionTypeTitle, rowIndex);
                this.getModel().setValue(COL_OPTION_A, fullQuestion.getString("dyh7_option_a"),rowIndex);
                this.getModel().setValue(COL_OPTION_B, fullQuestion.getString("dyh7_option_b"),rowIndex);
                this.getModel().setValue(COL_OPTION_C, fullQuestion.getString("dyh7_option_c"),rowIndex);
                this.getModel().setValue(COL_OPTION_D, fullQuestion.getString("dyh7_option_d"),rowIndex);
                this.getModel().setValue(COL_OPTION_E, fullQuestion.getString("dyh7_option_e"),rowIndex);
                this.getModel().setValue(COL_Score_value, fullQuestion.getString("dyh7_score_value"),rowIndex);
//                this.getModel().setValue(COL_single_selection, fullQuestion.getString("dyh7_radioclass"),rowIndex);
                // 单选题选项映射
                String selectionValue = fullQuestion.getString("dyh7_radioclass");
                String selectionTitle = YES_NO_MAP.getOrDefault(selectionValue, " ");
                this.getModel().setValue(COL_single_selection, selectionTitle, rowIndex);
            }
        }
    }


}