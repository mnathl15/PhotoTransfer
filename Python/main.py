
import bluetooth
import random

HOST = ''          # Symbolic name
PORT = 25
import os
import io
import sys
from PIL import Image
from PIL import ImageFile


print('Looking for connections...')

server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )


server_sock.bind(("",PORT))
server_sock.listen(PORT)

client_sock,address = server_sock.accept()
print ("Accepted connection from ",address)





def save_photo(data):
     ImageFile.LOAD_TRUNCATED_IMAGES = True
     orginal_image = Image.open(io.BytesIO(data))


     rand_file_num = random.randint(1,1000)

     while(os.path.isfile("C:/Users/Michael/Desktop/PhotoTransfer/image%d.png" %rand_file_num)):
          rand_file_num = random.randint(1,1000)


     orginal_image.save("C:/Users/Michael/Desktop/PhotoTransfer/image%d.png" %rand_file_num)



def load_photo():
     data = client_sock.recv(5000)
     if(len(data) > 0):
          while(True):
               chunk = client_sock.recv(5000)
               if (len(chunk) == 0):
                    save_photo(data)

                    break
               data+=chunk





load_photo()

server_sock.close()
client_sock.close()

#restarts the script to re-accept connections
os.execv(sys.executable, [sys.executable] + sys.argv)










