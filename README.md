# PatchCritical
Salut a tous, voici mon premier post sur github, pas ouf j'en suis certain mais bon passons au sujet.


J'ai fait une classe qui permet de patch les critiques de minecraft qui augmente de 50% les dégats.


Cela permettra de passer ce pourcentage a moins ou plus... (Exemple : 50% => 30%).


> Voici un forum spigot qui m'a aider a faire ceci : https://www.spigotmc.org/threads/how-to-get-attack-damage-attributemodifier-from-an-itemstack-as-displayed-on-items-in-game.284455/


> Pas besoin de me créditer, honnêtement je m'en fiche.


Bon jeu a tous.

> Exemple d'utilisation : 
```
@EventHandler 
public void patchEffects(EntityDamageByEntityEvent event) {
new PatchCritical(event,30);
}
```

> Avec Critique aléatoire : 
```
@EventHandler
    public void patchEffects(EntityDamageByEntityEvent event) {
     int i = new Random().nextInt(11);
       i += 40;
        new PatchCritical(event,i);
 }
 ```
