package com.togedog.eventListener;

import com.togedog.eventListener.EventCaseEnum.EventCase;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class CustomEvent extends ApplicationEvent {
    private EventCase methodName;
    private Object resource;
    private List<Long> resources;

    public CustomEvent(Object source, EventCase methodName,Object resource) {
        super(source);
        this.methodName = methodName;
        this.resource = resource;
    }public CustomEvent(Object source, EventCase methodName,List<Long> resources) {
        super(source);
        this.methodName = methodName;
        this.resources = resources;
    }
}
