import socket


class ServerHandler:

    # ServerHandler constructor
    def __init__(self, address):
        self.address = address
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # self.s.connect((self.address, 8000))

    # Connects to server
    def connect(self):
        self.s.connect((self.address, 8000))

    # Sends username and password to server and receives the ack
    def login(self, username, password):
        self.s.send(f'login {username} {password}\n'.encode('utf-8'))
        res = self.s.recv(1024).decode("utf-8")
        return res

    # Sends a request to a server for IDs of connected sensors
    def getIDS(self):
        self.s.send('getids\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    # Sends a request to a server for data,for a specific ID
    def getData(self, ID, amount):
        self.s.send(f'getdata {ID} {amount}\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    # Sends a request to a server to get range for a specific sensor
    def getRange(self, ids: object) -> object:
        self.s.send(f'getrange {ids}\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    # Closes socket
    def closeSocket(self):
        self.s.close()