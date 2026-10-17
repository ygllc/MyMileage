#!/bin/bash
# Pull down Omarchy configurations/dotfiles into the home directory
git clone https://github.com ~/.local/share/omarchy

# Download and extract Android Studio stable
mkdir -p ~/android-studio
wget -qO- https://gvt1.com | tar -xz -C ~/android-studio --strip-components=1

# Apply Omarchy patch for JetBrains IDE focus issues
mkdir -p ~/.local/share/omarchy/default/hypr/apps/
cat << 'EOF' >> ~/.local/share/omarchy/default/hypr/apps/jetbrains.conf
windowrulev2 = tag +jb, class:^jetbrains-.+$,floating:1
windowrulev2 = stayfocused, tag:jb
windowrulev2 = noinitialfocus, tag:jb
EOF
