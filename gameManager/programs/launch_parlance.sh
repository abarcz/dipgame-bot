#!/bin/bash

#Script to configure the launching settings of parlance server

NO_ARGS=0
E_OPTERROR=65
options=l:m:r:b:hg:v:V:
LVL=0
MTL=300
RTL=120
BTL=120
GAMES=1
VERBOSITY=0
VARIANT="standard"
FILE="parlance.cfg"

#
# Function to print help
#
print_help_uu()
{
	   echo "Usage: $0 [OPCIÓN]..."; 
	   echo "Where -l <LVL>		Syntax level of the game.";
	   echo "      -m <MTL>		Time limit for movement phases, in seconds.";
	   echo "      -r <RTL>		Time limit for retreat phases, in seconds.";
	   echo "      -b <BTL>		Time limit for build phases, in seconds.";
	   echo "      -g <GAMES>	Number of games to play in server. By default 1.";
	   echo "      -v <VERBOSITY>	Verbosity level. By default 0.";
	   echo "      -V <VARIANT>	The map to use for games.";
	   echo "A setting of 0 disables the corresponding time limits.";
	   echo "";
	   echo "Default configuration:";
	   print_conf_values;
	   echo "GAMES = "$GAMES;
	   echo "VERBOSITY = "$VERBOSITY;
	   echo "VARIANT = "$VARIANT;
	   echo "";
	   
	   return
}

#
# Function to print configuration file header
#
print_conf_header()
{
echo "# Example configuration file for Parlance, a Diplomacy framework
# 
# This file may be saved as parlance.cfg in either $HOME/.config or
# the program working directory; settings in the latter will override
# those in the former.
# 
# Lines starting with hash marks (#) or semicolons (;) are ignored,
# and may be used for comments.  In this sample file, semicolons are
# used to show the default setting for each option.
# 
# The sections are ordered in approximate likelihood of customization.

"
}

#
# Function to print game configuration values
#
print_conf_game_values()
{
	echo "LVL = "$LVL
	echo "MTL = "$MTL
	echo "RTL = "$RTL
	echo "BTL = "$BTL
	return
}

#
# Function to print server configuration values
#
print_conf_server_values()
{
	return
}

#
# Function to print judge configuration values
#
print_conf_judge_values()
{
	return
}

#
# Function to print clients configuration values
#
print_conf_clients_values()
{
	return
}

#
# Function to print network configuration values
#
print_conf_network_values()
{
	return
}

#
# Function to print main configuration values
#
print_conf_main_values()
{
	return
}

#
# Function to print datc configuration values
#
print_conf_datc_values()
{
	return
}

#
# Function to print tokens configuration values
#
print_conf_tokens_values()
{
	return
}

#
# Function to print syntax configuration values
#
print_conf_syntax_values()
{
	return
}

#
# Main programm
#

if [ $# -eq "$NO_ARGS" ] # should check for no arguments
then
	clear
	echo "Running default configuration..."
fi

while getopts $options Option
do
	case $Option in
	l) LVL="$OPTARG";;
	m) MTL="$OPTARG";;
	r) RTL="$OPTARG";;
	b) BTL="$OPTARG";;
	g) GAMES="$OPTARG";;
	v) VERBOSITY="$OPTARG";;
	V) VARIANT="$OPTARG";;
	h) print_help_uu; exit 1;;
	\?) print_help_uu; exit 1;;
	esac
done

if [ $# -ne "$NO_ARGS" ]
then
	clear
fi

print_conf_header > $FILE
echo "[game]" >> $FILE
print_conf_game_values >> $FILE
echo "[server]" >> $FILE
print_conf_server_values  >> $FILE
echo "[judge]" >> $FILE
print_conf_judge_values  >> $FILE
echo "[clients]" >> $FILE
print_conf_clients_values  >> $FILE
echo "[network]" >> $FILE
print_conf_network_values  >> $FILE
echo "[main]" >> $FILE
print_conf_main_values  >> $FILE
echo "[datc]" >> $FILE
print_conf_datc_values  >> $FILE
echo "[tokens]" >> $FILE
print_conf_tokens_values  >> $FILE
echo "[syntax]" >> $FILE
print_conf_syntax_values  >> $FILE

mv $FILE $HOME/.config

parlance-server -g$GAMES -v$VERBOSITY $VARIANT


