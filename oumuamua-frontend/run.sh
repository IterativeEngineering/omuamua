#!/bin/bash
#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Install dependencies and build the frontend application
docker build . -t omuamua-frontend

docker run --rm --network=host --name=omuamua-frontend omuamua-frontend
