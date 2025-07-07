package Student;

import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.ILocaleString;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.message.api.MessageChannels;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;
import kd.sdk.plugin.Plugin;

import java.util.*;

/**
 * 单据界面插件
 */
public class Academic_warning extends AbstractBillPlugIn implements Plugin {

    private static final String TYPE_WARNING = "warning";

    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("dyh7_warning")) {
            // 构建消息体发送
            MessageInfo message = new MessageInfo();
            // 信息title
            ILocaleString title = new LocaleString();
            ILocaleString localeTaskName = new LocaleString("测试任务数据80");
            title.setLocaleValue_en("urge to do " + localeTaskName.getLocaleValue_zh_CN());
            title.setLocaleValue_zh_CN("您有《" + localeTaskName.getLocaleValue_zh_CN() + "》待处理！");
            title.setLocaleValue_zh_TW("");
            message.setMessageTitle(title);
            // 信息主体
            ILocaleString content = new LocaleString();
            content.setLocaleValue_en("you need to report this job!");
            content.setLocaleValue_zh_CN("请尽快上报");
            content.setLocaleValue_zh_TW("");
            message.setMessageContent(content);
            // 信息接收人
            ArrayList<Long> receivers = new ArrayList<Long>();
            receivers.add(RequestContext.get().getCurrUserId());
            message.setUserIds(receivers);
            // 信息标签
            ILocaleString tag = new LocaleString();
            tag.setLocaleValue_zh_CN("待办任务催办");
            //message.setTag(ResManager.loadKDString("待办任务催办", "TaskFollowFormPlugin_3", ModuleConstant.MODULE_FORM_PLUGIN));
            message.setMessageTag(tag);
            // 信息发送人
            message.setSenderId(RequestContext.get().getCurrUserId());
            message.setType(MessageInfo.TYPE_MESSAGE);
            StringBuilder notifyType = new StringBuilder();
            notifyType.append(MessageChannels.YUNZHIJIA).append(",");
            notifyType.append(MessageChannels.SMS);
            message.setNotifyType(notifyType.toString());
            MessageCenterServiceHelper.sendMessage(message);

        }
    }
}