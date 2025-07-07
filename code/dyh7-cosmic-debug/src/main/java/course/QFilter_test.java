package course;

import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 单据界面插件
 */
public class QFilter_test extends AbstractBillPlugIn implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("dyh7_course_anal")) {

            QFilter qFilter1 = new QFilter("name", QCP.equals, "A1-101");
            QFilter qFilter2 = new QFilter("dyh7_c_time", QCP.equals, "周三上午");
            QFilter qFilter3 = new QFilter("dyh7_status", QCP.equals, "0");
            QFilter qFilter4 = new QFilter("dyh7_capacity",QCP.large_equals, 100);
            // 组合条件：AND关系
            QFilter totalFilter = qFilter1
                    .and(qFilter2)
                    .and(qFilter3)
                    .and(qFilter4);
            // 使用load方法查询多条数据（最多1000条）
            String selectProperties = "id,dyh7_capacity";
            DynamicObject[] results = BusinessDataServiceHelper.load(
                    "dyh7_c_scheduling",  // 实体标识
                    selectProperties,    // 返回的字段（仅需id即可统计数量）
                    new QFilter[]{totalFilter}// 查询条件
            );

            String dyh7_isbn = results[0].getString("name");
            //String dyh7_teachername = results[0].getString("name");
            //String roomCapacity = results[0].getString("dyh7_capacity");

            // 3. 处理结果
            if (results.length > 0) {
                int capacity = results[0].getInt("dyh7_capacity");
                //this.getView().showMessage("教室容量: " + capacity);
                this.getView().showMessage("查询到数据条数: " + capacity+results[0]);
            } else {
                // 先检查教室是否存在（您确认有10条记录，这步应该不会触发）
                QFilter qFilter5 = new QFilter("name", QCP.equals, "A1-101");
                QFilter qFilter6 = new QFilter("dyh7_c_time", QCP.equals, "周一上午");
                QFilter qFilter7 = new QFilter("dyh7_status", QCP.equals, "0");
                QFilter qFilter8 = new QFilter("dyh7_capacity",QCP.large_equals, 100);
                DynamicObject[] roomCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter5}
                );

                if (roomCheck.length == 0) {
                    this.getView().showMessage("无该教室"+roomCheck.length);
                    return;
                }

                // 检查时间+教室组合
                DynamicObject[] timeCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter5.and(qFilter6)}
                );

                if (timeCheck.length == 0) {
                    this.getView().showMessage("该教室在周一上午不可用"+timeCheck.length);
                    return;
                }

                // 检查状态
                DynamicObject[] statusCheck = BusinessDataServiceHelper.load(
                        "dyh7_c_scheduling", "id", new QFilter[]{qFilter5.and(qFilter6).and(qFilter7)}
                );

                if (statusCheck.length == 0) {
                    this.getView().showMessage("该教室在周一上午已被占用"+statusCheck.length);
                    return;
                }
                // 最后检查容量
                this.getView().showMessage("教室容量不足100人");
            }
        }
    }
}