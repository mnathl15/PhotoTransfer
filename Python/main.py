from bluetooth import *
import bluetooth

HOST = ''          # Symbolic name
PORT = 25
import os
import io
from PIL import Image
from PIL import ImageFile


# print ("performing inquiry...")
#
#
# try:
#      nearby_devices = discover_devices(lookup_names = True)
#      print("found %d devices" % len(nearby_devices))
#      for name, addr in nearby_devices:
#           print(" %s - %s" % (addr, name))
# except:
#      print("Can't find")





print('Looking for connections...')

server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )


server_sock.bind(("",PORT))
server_sock.listen(PORT)

client_sock,address = server_sock.accept()
print ("Accepted connection from ",address)




data = client_sock.recv(1000)



while(True):
     print('tr')
     chunk = client_sock.recv(1000)
     if (len(chunk) == 0):
          break
     data+=chunk



print("received [%s]" % data)

print(len(data))

# ImageFile.LOAD_TRUNCATED_IMAGES = True
# orginal_image = Image.open(io.BytesIO(data))
#
# orginal_image.save("C:/Users/Michael/Desktop/image4.png")


client_sock.close()
server_sock.close()






