package takakumashuka.trial.linebeacon.controller;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.beacon.BeaconContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class MainController {

    @Autowired
    private LineMessagingService lineMessagingService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello " + new Date().toString();
    }

    @PostMapping("/callback")
    public void callback(@LineBotMessages List<Event> events) throws IOException {
        for (Event event : events) {
            System.out.println("event: " + event);
            ReplyMessage replyMessage = null;

            if (event instanceof MessageEvent) {
                replyMessage = handleMessageEvent((MessageEvent) event);
            }
            else if (event instanceof BeaconEvent) {
                replyMessage = handleBeaconEvent((BeaconEvent) event);
            }

            if (replyMessage != null) {
                BotApiResponse apiResponse = lineMessagingService.replyMessage(replyMessage).execute().body();
                System.out.println("Sent messages: " + apiResponse);
            }
        }
    }

    private ReplyMessage handleMessageEvent(MessageEvent event) throws IOException {
        Message replyMessage = null;
        MessageContent message = event.getMessage();

        if (message instanceof TextMessageContent) {
            TextMessageContent textMessageContent = (TextMessageContent) message;
            String text = textMessageContent.getText();

            if (text.equals("user")) {
                UserProfileResponse profile = lineMessagingService.getProfile(event.getSource().getUserId()).execute().body();
                text = String.format("userId:%s, displayName:%s", profile.getUserId(), profile.getDisplayName());
            }

            replyMessage = new TextMessage(text);
        }
        else if (message instanceof ImageMessageContent) {
            replyMessage = new TextMessage("画像を受け取りました。");
        }
        else {
            replyMessage = new TextMessage("[SYSTEM] received MessageEvent");
        }

        return new ReplyMessage(event.getReplyToken(), replyMessage);
    }

    private ReplyMessage handleBeaconEvent(BeaconEvent event) {
        BeaconEvent beaconEvent = (BeaconEvent) event;
        BeaconContent beacon = beaconEvent.getBeacon();
        return new ReplyMessage(beaconEvent.getReplyToken(), new TextMessage("Beacon受信!! [" + beacon + "]"));
    }

}
