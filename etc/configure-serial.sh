#!/bin/zsh

if [[ -d /var/spool/uucp ]]; then 
   echo This directory has already existed.;
else
   sudo mkdir /var/spool/uucp;
fi

if [[ -d /var/lock ]]; then
    echo This directory has already existed.;
else
    sudo mkdir /var/lock;
fi

sudo chmod 775 /var/spool/uucp
sudo chmod 775 /var/lock
sudo chgrp uucp /var/spool/uucp
sudo chgrp uucp /var/lock

ls -l /var/spool/ | grep uucp
ls -l /var/ | grep lock

who -H am i

sudo dscl . -append /Groups/uucp GroupMembership ${PWD:t}
dscl . -read /Groups/uucp | grep GroupMembership