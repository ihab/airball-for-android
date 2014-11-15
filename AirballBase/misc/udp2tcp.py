#!/usr/bin/python

import socket
import sys
import Queue
import threading

LOCALHOST = '127.0.0.1'
TIMEOUT = 2  # seconds
BUFFER_SIZE = 4096
END_MARKER = b'airball'

########################################################################

class DrainingQueue:

    def __init__(self):
        self.lock = threading.Lock()
        self.q = Queue.Queue()

    def put(self, o):
        self.lock.acquire()
        try:
            self.q.put(o)
        finally:
            self.lock.release()

    def get(self):
        o = self.q.get()
        self.lock.acquire()
        try:
            while not self.q.empty():
                o = self.q.get()
        finally:
            self.lock.release()
        return o

########################################################################

class Worker:

    def __init__(self):
        pass

    def log(self, str):
        print(self.__class__.__name__ + ': ' + str)

########################################################################

class UdpListener(Worker):

    def __init__(self, port, q):
        self.port = port
        self.q = q

    def run(self):
        while True:
            try:
                self.log('Creating UDP socket ...')
                sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                try:
                    self.log('Binding to localhost UDP port ' + str(self.port) + ' ...')
                    sock.bind((LOCALHOST, self.port))
                    sock.settimeout(TIMEOUT)
                    self.log('Binding successful ...')
                    while True:
                        try:
                            self.q.put(sock.recv(BUFFER_SIZE))
                        except socket.timeout as e:
                            self.log('Socket timeout (' + str(TIMEOUT) + ' seconds): ' + str(e))
                        except Exception as e:
                            self.log('Exception in recv, rebinding: ' + str(e))
                            break
                except Exception as e:
                    self.log('Exception, closing UDP socket: ' + str(e))
                finally:
                    sock.close()
            except Exception as e:
                self.log('Exception in creating UDP socket: ' + str(e))
                    
########################################################################
        
class TcpSender(Worker):

    def __init__(self, port, q):
        self.port = port
        self.q = q

    def run(self):
        while True:
            try:
                self.log('Creating TCP socket ...')
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                try:
                    self.log('Connecting to localhost on port ' + str(self.port))
                    sock.connect((LOCALHOST, self.port))
                    sock.settimeout(TIMEOUT)
                    self.log('Connection successful ...')
                    while True:
                        try:
                            sock.send(self.q.get() + END_MARKER)
                        except socket.timeout as e:
                            self.log('Socket timeout (' + str(TIMEOUT) + ' seconds): ' + str(e))
                        except Exception as e:
                            self.log('Exception in recv, rebinding: ' + str(e))
                            break
                except Exception as e:
                    self.log('Exception, closing TCP socket: ' + str(e))
                finally:
                    sock.close()
            except Exception as e:
                self.log('Exception in creating TCP socket: ' + str(e))

########################################################################

def start(worker):
    threading.Thread(None, worker.run).start()
    
def run(receive_port, send_port):
    q = DrainingQueue()
    start(UdpListener(receive_port, q))
    start(TcpSender(send_port, q))

########################################################################

def usage():
    print('Usage: ' + sys.argv[0] + ' <receive_port> <send_port>')

if not (len(sys.argv) == 3):
    usage()
else:
    try:
        run(int(sys.argv[1]), int(sys.argv[2]))
    except ValueError as e:
        usage()
        print(e)
