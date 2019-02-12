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


data = client_sock.recv(100000)


print("received [%s]" % data)

print(len(data))




with open("C:/Users/Michael/Desktop/image.png", "wb") as f:
     f.write(data)

# orginal_image = Image.open(data)
#
# orginal_image.save("C:/Users/Michael/Desktop/image.png")


client_sock.close()
server_sock.close()






