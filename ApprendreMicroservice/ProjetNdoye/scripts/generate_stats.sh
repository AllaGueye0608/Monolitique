#!/bin/bash
# Génère des statistiques à partir des logs de Nagios
LOG_FILE="/usr/local/nagios/var/nagios.log"

# Compter le nombre d'alertes CRITICAL et WARNING
critical_count=$(grep -c "CRITICAL" "$LOG_FILE")
warning_count=$(grep -c "WARNING" "$LOG_FILE")

# Afficher les statistiques
echo "Statistiques des alertes :"
echo " - CRITICAL : $critical_count"
echo " - WARNING : $warning_count"