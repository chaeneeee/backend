package com.togedog.eventListener;

import com.togedog.chatRoom.service.ChatRoomService;
import com.togedog.matching.service.MatchingService;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.matchingStandBy.service.MatchingStandByService;
import com.togedog.member.service.MemberService;
import com.togedog.redis.MarkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceEventListener {
    private final MatchingService matchingService;
    private final ChatRoomService chatRoomService;
    private final MatchingStandByService matchingStandByService;
    private final MemberService memberService;
    private final MarkerService markerService;

    @EventListener
    public void handleMyCustomEvent(CustomEvent event) {
        switch (event.getMethodName()) {
            case DELETE_RELATED_MATCHING_STAND_BY_DATA:
                log.debug("Event : DELETE_RELATED_MATCHING_STAND_BY_DATA");
                matchingStandByService.extractedDataAndChangeStatusToFail((Long) event.getResource(),
                        MatchingStandBy.Status.STATUS_WAIT);
                break;
            case SUCCESS_RELATED_MATCHING_DATA:
                log.debug("Event : SUCCESS_RELATED_MATCHING_DATA");
                matchingService.updateMatchForCustomEvent(event.getResources().get(0),event.getResources().get(1));
                break;

            case CREATE_CHAT_ROOM:
                log.debug("Event : CREATE_CHAT_ROOM");
                chatRoomService.createChatRoom(memberService.createChatRoomForCustomEvent(event.getResources()));
                break;
            case DELETE_MARKER:
                log.debug("Event : DELETE_MARKER");
                String markerInfo = "marker:" + (String)event.getResource();
                markerService.deleteMarker(markerInfo);


        }
    }
}
