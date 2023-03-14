SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

pushd $SCRIPT_DIR
echo "Installing deps..."
/etc/poetry/bin/poetry install

echo "Starting server..."
/etc/poetry/bin/poetry run python model_server
