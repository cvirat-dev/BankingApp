// Lokaler Docker-Betrieb (docker compose): Die URLs werden vom Browser des Hosts aufgerufen,
// daher localhost + die in docker-compose.yml veröffentlichten Ports (nicht die Container-Namen).
export const environment = {
  production: true,
  kontoServiceUrl: 'http://localhost:8081',
  benachrichtigungServiceUrl: 'http://localhost:8082'
};
