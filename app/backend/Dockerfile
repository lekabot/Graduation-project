FROM python:3.9

RUN mkdir /apping

WORKDIR ./apping

COPY requirements.txt .

RUN pip install -r requirements.txt

COPY . .

WORKDIR ./src

RUN chmod a+x ../docker/*.sh

#CMD ["python", "./tests/test_data.py"]