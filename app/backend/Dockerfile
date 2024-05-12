FROM python:3.9

RUN mkdir /apping

WORKDIR ./apping

COPY requirements.txt .

RUN pip install -r requirements.txt

COPY . .

WORKDIR ./src

RUN chmod a+x ../docker/*.sh

CMD ["gunicorn", "main:app", "--workers", "1", "--worker-class", "uvicorn.workers.UvicornWorker", "--bind=0.0.0.0:8000"]