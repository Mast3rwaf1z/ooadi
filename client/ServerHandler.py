import socket


class ServerHandler:
    def __init__(self, address):
        self.address = address
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        #self.s.connect((self.address, 8000))

    def connect(self):
        self.s.connect((self.address, 8000))

    def login(self, username, password):
        self.s.send(f'login {username} {password}\n'.encode('utf-8'))
        print("what")
        res = self.s.recv(1024).decode("utf-8")
        return res

    def getIDS(self):
        self.s.send('getids\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    def getData(self, everyID, counter, amount):
        self.s.send(f'getdata {everyID[counter]} {amount}\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    def getDataDiff(self, ID , amount):
        self.s.send(f'getdata {ID} {amount}\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    def getRange(self, ids):
        self.s.send(f'getrange {ids}\n'.encode('utf-8'))
        return self.s.recv(1024).decode("utf-8")

    def closeSocket(self):
        self.s.close()

    #def getID(self):
    #    s.send('getids\n'.encode('utf-8'))

    #    recv = s.recv(1024).decode("utf-8")
    #    dataRec = recv[recv.find(":") + 1:]
    #    return dataRec.split(" ")


