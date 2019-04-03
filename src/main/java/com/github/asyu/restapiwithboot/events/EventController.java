package com.github.asyu.restapiwithboot.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.asyu.restapiwithboot.common.ErrorsResource;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;
    
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        
        eventValidator.validate(eventDto, errors);
        
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        //eventResource.add(new Link("/docs/index.html#resources-events-create"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }
    
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResources = assembler.toResource(page, e -> new EventResource(e));
        //pagedResources.add(new Link("/docs/index.html#resources-events-list"));
        return ResponseEntity.ok(pagedResources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity queryEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        //eventResource.add(new Link("/docs/index.html#resources-events-create"));
        return ResponseEntity.ok(eventResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        
        eventValidator.validate(eventDto, errors);
        
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        
        Event existingEvent = optionalEvent.get(); 
        this.modelMapper.map(eventDto, existingEvent);
        this.eventRepository.save(existingEvent);
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(existingEvent.getId());
        EventResource eventResource = new EventResource(existingEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        //eventResource.add(new Link("/docs/index.html#resources-events-update"));
        
        return ResponseEntity.ok(eventResource);
    }
    
    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
    
}