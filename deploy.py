import os
import time

os.system("rm -rf /var/lib/tomcat9/webapps/adm.war")
print("Removed War")
time.sleep(10)
os.system("cp /home/ubuntu/adm.war /var/lib/tomcat9/webapps/adm.war")
print("Copy War")
time.sleep(10)
print("Restart Complete")