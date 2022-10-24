from random import randint
from socket import AF_INET, SOCK_STREAM, socket
from sys import argv
from threading import Thread
from time import sleep

sleep(.1) #a little bit of delay to make sure the server is running
s = socket(AF_INET, SOCK_STREAM)
print("Connecting to server...")
s.connect((argv[1], 8888))
print("Successfully connected to server")

id = argv[2]
s.send(bytes(id+"\n", encoding="utf-8"))

sleep(1)
while True:
    request = s.recv(1024).decode("utf-8")
    print("Server requested data")
    data = randint(-10, 10)
    s.send(bytes(str(data)+"\n", encoding="utf-8"))
    print("sent data to the server")
    sleep(1)