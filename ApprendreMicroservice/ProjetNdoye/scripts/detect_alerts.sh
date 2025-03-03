#!/bin/bash
# Analyse les logs de Nagios pour détecter les alertes
LOG_FILE="/usr/local/nagios/var/nagios.log"

# Chercher les lignes contenant "CRITICAL" ou "WARNING"
grep -E "CRITICAL|WARNING" "$LOG_FILE"