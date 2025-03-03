import tkinter as tk
from tkinter import ttk, messagebox, Text, Scrollbar
import ttkbootstrap as tb  # Module pour améliorer le design avec un thème Bootstrap
import subprocess
import webbrowser

# Initialisation de l'application
app = tb.Window(themename="darkly")  # Application avec un thème Bootstrap sombre
app.title("Supervision Réseau avec Nagios")  # Titre de la fenêtre principale
app.geometry("900x600")  # Dimensions de la fenêtre principale

# 🟢 Fonction pour démarrer Nagios et ouvrir l'interface Web
def start_nagios():
    try:
        subprocess.run(["./scripts/start_nagios.sh"])  # Exécution du script pour démarrer Nagios
        webbrowser.open("http://localhost/nagios")  # Ouverture de l'interface web de Nagios

        # Création d'une fenêtre modale indiquant que Nagios a démarré avec succès
        success_window = tk.Toplevel(app)
        success_window.title("Succès")
        success_window.geometry("300x100")

        label = ttk.Label(success_window, text="Nagios démarré avec succès !", font=("Arial", 14))
        label.pack(expand=True)

        success_window.after(3000, success_window.destroy)  # La fenêtre se ferme après 3 secondes

    except Exception as e:
        messagebox.showerror("Erreur", f"Erreur : {e}")  # En cas d'erreur, une boîte d'alerte s'affiche

# 🟢 Fonction pour superviser un fichier de configuration (Mode Fenêtre Modale)
def supervise_file(file_path, window_title):
    # Création d'une fenêtre modale pour afficher et éditer un fichier de configuration
    supervise_window = tb.Toplevel(app)
    supervise_window.title(window_title)
    supervise_window.geometry("850x600")
    supervise_window.transient(app)  # Empêche la fenêtre d'apparaître en dehors de l'application principale
    supervise_window.grab_set()  # Rendre cette fenêtre modale (bloque l'accès à l'application principale)

    frame = ttk.Frame(supervise_window, padding=10)
    frame.pack(fill=tk.BOTH, expand=True)

    # Zone de texte pour afficher le contenu du fichier
    file_content_text = Text(frame, wrap=tk.WORD, font=("Courier", 12), bg="#222", fg="#fff", insertbackground="white")
    file_content_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

    # Barre de défilement pour la zone de texte
    scrollbar = Scrollbar(frame, command=file_content_text.yview)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
    file_content_text.config(yscrollcommand=scrollbar.set)

    try:
        with open(file_path, "r") as file:
            content = file.read()  # Lecture du fichier
            file_content_text.insert(tk.END, content)  # Insertion du contenu dans la zone de texte
    except Exception as e:
        messagebox.showerror("Erreur", f"Impossible de lire le fichier : {e}")  # Affichage de l'erreur en cas d'échec

    # Fonction pour enregistrer le contenu modifié dans le fichier
    def save_file():
        try:
            new_content = file_content_text.get(1.0, tk.END)  # Récupérer le contenu modifié
            with open(file_path, "w") as file:
                file.write(new_content)  # Sauvegarder dans le fichier

            # Fenêtre de succès après enregistrement
            success_window = tk.Toplevel(supervise_window)
            success_window.title("Succès")
            success_window.geometry("300x100")
            success_window.transient(supervise_window)  # La fenêtre de succès est modale
            success_window.grab_set()

            label = ttk.Label(success_window, text="Fichier enregistré avec succès !", font=("Arial", 14))
            label.pack(expand=True)

            success_window.after(3000, success_window.destroy)  # La fenêtre se ferme après 3 secondes

            # Redémarrer Nagios après modification
            subprocess.run(["./scripts/restart_nagios.sh"])

        except Exception as e:
            messagebox.showerror("Erreur", f"Impossible d'enregistrer le fichier : {e}")  # Erreur d'enregistrement

    # Cadre pour les boutons "Enregistrer" et "Fermer"
    button_frame = ttk.Frame(supervise_window)
    button_frame.pack(fill=tk.X, pady=10)

    save_button = tb.Button(button_frame, text="Enregistrer", bootstyle="success", command=save_file)
    save_button.pack(side=tk.LEFT, padx=10)

    close_button = tb.Button(button_frame, text="Fermer", bootstyle="danger", command=supervise_window.destroy)
    close_button.pack(side=tk.RIGHT, padx=10)

    supervise_window.wait_window()  # Attend la fermeture de la fenêtre avant de rendre l'accès à la fenêtre principale

# 🟢 Fonctions pour la gestion de Nagios
def restart_nagios():
    try:
        subprocess.run(["./scripts/restart_nagios.sh"])  # Redémarre Nagios
        messagebox.showinfo("Succès", "Nagios redémarré avec succès !")
    except Exception as e:
        messagebox.showerror("Erreur", f"Erreur : {e}")  # Affiche une erreur si l'opération échoue

def stop_nagios():
    try:
        subprocess.run(["./scripts/stop_nagios.sh"])  # Arrête Nagios
        messagebox.showinfo("Succès", "Nagios arrêté avec succès !")
    except Exception as e:
        messagebox.showerror("Erreur", f"Erreur : {e}")  # Affiche une erreur en cas d'échec

# 🟢 Fonctions pour l'analyse réseau
def launch_wireshark():
    try:
        subprocess.run(["./scripts/launch_wireshark.sh"])  # Lance Wireshark pour l'analyse réseau
        messagebox.showinfo("Succès", "Wireshark lancé avec succès !")
    except Exception as e:
        messagebox.showerror("Erreur", f"Erreur : {e}")  # Affiche une erreur si le lancement échoue

def detect_alerts():
    try:
        result = subprocess.run(["./scripts/detect_alerts.sh"], capture_output=True, text=True)  # Exécute le script de détection d'alertes
        output_text.delete(1.0, tk.END)  # Efface le texte précédent

        # Si le script a réussi, analyse les alertes et colorie les résultats
        if result.returncode == 0:
            # Définir les balises pour la coloration du texte
            output_text.tag_configure("warning", foreground="orange")  # Orange pour les alertes warning
            output_text.tag_configure("critical", foreground="red")  # Rouge pour les alertes critical

            # Récupère les lignes du résultat
            lines = result.stdout.splitlines()
            warning_count = 0
            critical_count = 0

            # Parcourir les lignes pour compter les alertes
            for line in lines:
                if "warning" in line.lower():  # Si l'alerte est de type warning
                    warning_count += 1
                elif "critical" in line.lower():  # Si l'alerte est de type critical
                    critical_count += 1

            # Afficher le résumé des alertes
            output_text.insert(tk.END, f"État des services :\n", "bold")
            output_text.insert(tk.END, f"Nombre total d'alertes : {warning_count + critical_count}\n")
            output_text.insert(tk.END, f"Alertes critiques (Critical) : {critical_count}\n", "critical")
            output_text.insert(tk.END, f"Alertes d'avertissement (Warning) : {warning_count}\n", "warning")
            output_text.insert(tk.END, "\nDétails des alertes :\n", "bold")

            # Afficher les détails des alertes
            for line in lines:
                if "warning" in line.lower():
                    output_text.insert(tk.END, line + "\n", "warning")
                elif "critical" in line.lower():
                    output_text.insert(tk.END, line + "\n", "critical")
                else:
                    output_text.insert(tk.END, line + "\n")
        else:
            output_text.insert(tk.END, "Aucune alerte détectée.\n")

    except Exception as e:
        output_text.insert(tk.END, f"Erreur : {e}")  # Affiche une erreur si la détection échoue

def generate_stats():
    try:
        result = subprocess.run(["./scripts/generate_stats.sh"], capture_output=True, text=True)  # Exécute le script pour générer des statistiques
        output_text.delete(1.0, tk.END)
        output_text.insert(tk.END, result.stdout if result.returncode == 0 else "Erreur lors de la génération des statistiques.")
    except Exception as e:
        output_text.insert(tk.END, f"Erreur : {e}")  # Affiche une erreur si la génération des statistiques échoue

# 🟢 Menu principal
menu_bar = tk.Menu(app)
app.config(menu=menu_bar)  # Attache le menu à l'application

# Menu "Nagios"
nagios_menu = tk.Menu(menu_bar, tearoff=0)
nagios_menu.add_command(label="Démarrer Nagios", command=start_nagios)
nagios_menu.add_command(label="Arrêter Nagios", command=stop_nagios)
menu_bar.add_cascade(label="Nagios", menu=nagios_menu)

# Menu "Supervision"
supervision_menu = tk.Menu(menu_bar, tearoff=0)
config_files = ["commands.cfg", "localhost.cfg", "switch.cfg", "timeperiods.cfg", "contacts.cfg", "printer.cfg", "templates.cfg", "windows.cfg"]

# Ajouter chaque fichier de configuration au menu
for file in config_files:
    supervision_menu.add_command(label=file, command=lambda f=file: supervise_file(f"/usr/local/nagios/etc/objects/{f}", f"Supervision {f}"))

menu_bar.add_cascade(label="Supervision", menu=supervision_menu)

# Menu "Outils"
tools_menu = tk.Menu(menu_bar, tearoff=0)
tools_menu.add_command(label="Lancer Wireshark", command=launch_wireshark)
menu_bar.add_cascade(label="Outils", menu=tools_menu)

# 🟢 Zone d'affichage des résultats
output_frame = ttk.LabelFrame(app, text="Résultats d'analyse", padding=10)
output_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

# Zone de texte pour afficher les résultats des alertes ou des statistiques
output_text = Text(output_frame, wrap=tk.WORD, height=15, font=("Courier", 12), bg="#222", fg="#fff", insertbackground="white")
output_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

# Ajouter une balise pour le texte en gras
output_text.tag_configure("bold", font=("Courier", 12, "bold"))

scrollbar = Scrollbar(output_frame, command=output_text.yview)
scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
output_text.config(yscrollcommand=scrollbar.set)

# 🟢 Boutons d'action
action_frame = ttk.Frame(app)
action_frame.pack(fill=tk.X, padx=10, pady=10)

# Boutons pour détecter les alertes et générer des statistiques
btn_alerts = tb.Button(action_frame, text="Détecter les Alertes", bootstyle="info", command=detect_alerts)
btn_alerts.pack(side=tk.LEFT, padx=5)

btn_stats = tb.Button(action_frame, text="Générer des Statistiques", bootstyle="primary", command=generate_stats)
btn_stats.pack(side=tk.RIGHT, padx=5)

# 🟢 Fonction pour fermer l'application
def on_close():
    for window in app.winfo_children():
        if isinstance(window, tb.Toplevel):
            window.destroy()  # Ferme toutes les fenêtres modales avant de fermer l'application
    app.destroy()

app.protocol("WM_DELETE_WINDOW", on_close)

# Lancer l'application
app.mainloop()