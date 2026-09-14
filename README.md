# TaskMaster

## Team notifications with SSE

The app now exposes real-time team notifications through Server-Sent Events and regular REST APIs.

### Endpoints

- `GET /api/teams/notifications/stream` — open an SSE stream for the authenticated user
- `POST /api/teams/notifications/send/{teamId}/{recipientEmail}` — send a notification to a teammate in the team
- `GET /api/teams/notifications` — list received notifications, with optional `teamId` and `unreadOnly` filters
- `POST /api/teams/notifications/{notificationId}/read` — mark a notification as read

### Request body

```json
{
  "message": "Please review the latest task update"
}
```
