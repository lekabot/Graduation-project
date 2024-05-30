#!/bin/bash

cd ..

alembic upgrade head

cd src

python3 ./test_data.py

gunicorn main:app --workers 1 --worker-class uvicorn.workers.UvicornWorker --bind=0.0.0.0:8000

