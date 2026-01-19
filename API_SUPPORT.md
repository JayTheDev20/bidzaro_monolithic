# Support Agent API Documentation

This document lists all API endpoints available for **Support Agents**, focusing on ticket management and issue resolution.

## Authentication (`/auth`)

### Agent Login
**POST** `/auth/login`

## Ticket Management (`/support`)

### View Open Tickets
**GET** `/support/admin/tickets`
*Lists all unassigned or open tickets.*

### View My Assigned Tickets
**GET** `/support/admin/my-tickets`
*Lists tickets specifically assigned to the logged-in agent.*

### Assign Ticket
**POST** `/support/admin/tickets/{ticketId}/assign`
*Query Param: agentId (can assign to self or others)*
**Response:**
```json
{
  "success": true,
  "message": "Ticket assigned",
  "data": {
    "ticketId": "ticket_1",
    "assignedTo": "agent_1"
  }
}
```

### Update Ticket Status
**PATCH** `/support/admin/tickets/{ticketId}/status`
*Query Param: status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)*
**Response:**
```json
{
  "success": true,
  "message": "Status updated",
  "data": {
    "ticketId": "ticket_1",
    "status": "IN_PROGRESS"
  }
}
```

### Resolve Ticket
**POST** `/support/admin/tickets/{ticketId}/resolve`
*Query Param: resolutionNotes*
*Marks the ticket as resolved and adds final notes.*
**Response:**
```json
{
  "success": true,
  "message": "Ticket resolved",
  "data": {
    "ticketId": "ticket_1",
    "status": "RESOLVED"
  }
}
```

## User & Order Lookup

Support agents often need to look up user or order details to resolve tickets.

*   **GET** `/users/profile` (via Admin APIs if authorized)
*   **GET** `/orders/{orderId}` - View order details related to a ticket
