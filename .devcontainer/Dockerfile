FROM archlinux:latest

# Install base dependencies, X11/VNC prerequisites, and Java for Android
RUN pacman -Syu --noconfirm && \
    pacman -S --noconfirm git base-devel wget unzip jdk-openjdk libsvg libglu mesa libpulse xorg-server xorg-xinit

# Create a non-root developer user required for AUR / Omarchy setups
RUN useradd -m -G wheel -s /bin/bash developer && \
    echo "developer ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

USER developer
WORKDIR /home/developer
