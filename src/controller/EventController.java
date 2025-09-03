package controller;

import model.Event;
import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EventController {
    private List<Event> events;
    private UserController userController;
    
    public EventController(UserController userController) {
        this.events = new ArrayList<>();
        this.userController = userController;
    }
    

    public Event createEvent(String name, String description, LocalDateTime eventDateTime, UUID creatorId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode estar vazio");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do evento não pode estar vazio");
        }
        
        if (eventDateTime == null) {
            throw new IllegalArgumentException("Data e horário do evento não podem ser nulos");
        }
        
        if (creatorId == null) {
            throw new IllegalArgumentException("ID do criador não pode ser nulo");
        }

        User creator = userController.getUserById(creatorId);
        if (creator == null) {
            throw new IllegalArgumentException("Usuário criador não encontrado");
        }

        if (eventDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data do evento não pode ser no passado");
        }
        
        Event event = new Event(name.trim(), description.trim(), eventDateTime, creatorId);
        events.add(event);
        return event;
    }

    public boolean editEvent(UUID eventId, UUID userId, String newName, String newDescription, LocalDateTime newEventDateTime) {
        Event event = findEventById(eventId);
        if (event == null) return false;

        if (!event.getCreatorId().equals(userId)) {
            return false; // Apenas o criador pode editar
        }
        
        if (newName != null && !newName.trim().isEmpty()) {
            event.setName(newName.trim());
        }
        
        if (newDescription != null && !newDescription.trim().isEmpty()) {
            event.setDescription(newDescription.trim());
        }
        
        if (newEventDateTime != null) {

            if (newEventDateTime.isBefore(LocalDateTime.now())) {
                return false; // Data no passado
            }
            event.setEventDateTime(newEventDateTime);
        }
        
        return true;
    }

    public boolean deleteEvent(UUID eventId, UUID userId) {
        Event event = findEventById(eventId);
        if (event == null) return false;

        if (!event.getCreatorId().equals(userId)) {
            return false; // Apenas o criador pode deletar
        }
        
        return events.remove(event);
    }

    public Event getEventById(UUID eventId) {
        return findEventById(eventId);
    }

    public List<Event> getEventsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchName = name.toLowerCase().trim();
        return events.stream()
            .filter(event -> event.getName().toLowerCase().contains(searchName))
            .collect(Collectors.toList());
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
            .filter(event -> event.getEventDateTime().isAfter(now))
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
            .filter(event -> event.getEventDateTime().isBefore(now))
            .sorted((e1, e2) -> e2.getEventDateTime().compareTo(e1.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getTodayEvents() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
            .filter(event -> event.isToday())
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getTomorrowEvents() {
        return events.stream()
            .filter(event -> event.isTomorrow())
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getThisWeekEvents() {
        return events.stream()
            .filter(event -> event.isThisWeek())
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getEventsByCreator(UUID creatorId) {
        return events.stream()
            .filter(event -> event.getCreatorId().equals(creatorId))
            .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public List<Event> getUpcomingWeekEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusWeeks(1);
        
        return events.stream()
            .filter(event -> event.getEventDateTime().isAfter(now) && 
                           event.getEventDateTime().isBefore(weekFromNow))
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public List<Event> getEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        return events.stream()
            .filter(event -> event.getEventDateTime().isAfter(startDate) && 
                           event.getEventDateTime().isBefore(endDate))
            .sorted((e1, e2) -> e1.getEventDateTime().compareTo(e2.getEventDateTime()))
            .collect(Collectors.toList());
    }

    public String getEventStats() {
        if (events.isEmpty()) {
            return "Nenhum evento cadastrado";
        }
        
        long totalEvents = events.size();
        long upcomingEvents = getUpcomingEvents().size();
        long pastEvents = getPastEvents().size();
        long todayEvents = getTodayEvents().size();
        
        return String.format("Total de eventos: %d\nEventos futuros: %d\nEventos passados: %d\nEventos hoje: %d",
            totalEvents, upcomingEvents, pastEvents, todayEvents);
    }

    public String getEventStats(UUID eventId) {
        Event event = findEventById(eventId);
        if (event == null) return "Evento não encontrado";
        
        User creator = userController.getUserById(event.getCreatorId());
        String creatorName = creator != null ? creator.getName() : "Usuário desconhecido";
        
        return String.format("Nome: %s\nDescrição: %s\nData/Hora: %s\nCriador: %s\nCriado em: %s\nStatus: %s",
            event.getName(),
            event.getDescription(),
            event.getFormattedEventDate(),
            creatorName,
            event.getFormattedDate(),
            event.isPast() ? "Passado" : "Futuro"
        );
    }

    private Event findEventById(UUID eventId) {
        if (eventId == null) return null;
        
        for (Event event : events) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    public void clearAllEvents() {
        events.clear();
    }

    public int getTotalEvents() {
        return events.size();
    }
}
