# Planning Poker

## Hosting

### Docker Compose

```yaml
services:
  planningpoker:
    container_name: planningpoker
    image: ghcr.io/planningtcg/planning-poker:main
    restart: unless-stopped
    ports:
      - 5000:5000
  # For automatic updates
  watchtower:
    container_name: watchtower
    image: containrrr/watchtower
    restart: unless-stopped
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    # Command is optional
    command: --interval 30 --remove-volumes --cleanup
```
