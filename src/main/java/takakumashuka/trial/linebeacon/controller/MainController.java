package takakumashuka.trial.linebeacon.controller;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.beacon.BeaconContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
            if (event instanceof MessageEvent) {
                MessageContent message = ((MessageEvent) event).getMessage();
                if (message instanceof TextMessageContent) {
                    System.out.println("Sending reply message");
                    TextMessageContent textMessageContent = (TextMessageContent) message;
                    BotApiResponse apiResponse = lineMessagingService.replyMessage(
                            new ReplyMessage(((MessageEvent) event).getReplyToken(), new TextMessage(textMessageContent.getText()))
                    ).execute().body();
                    System.out.println("Sent messages: " + apiResponse);
                }
            }
            else if (event instanceof BeaconEvent) {
                BeaconEvent beaconEvent = (BeaconEvent) event;
                BeaconContent beacon = beaconEvent.getBeacon();
                BotApiResponse apiResponse = lineMessagingService.replyMessage(
                        new ReplyMessage(beaconEvent.getReplyToken(), new TextMessage("Beacon受信!! [" + beacon + "]"))
                ).execute().body();
                System.out.println("Received beacon: " + apiResponse);
            }
        }
    }

}
