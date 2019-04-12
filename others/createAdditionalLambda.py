import json

with open('armi.json') as json_file:  
    data = json.load(json_file)
    for p in data:
        a = p["additional"]
        if a != None:
            for b in a:
                print("data.put(\"" + b["lambdaIdentifier"] + "\", (pl, map, playerList)->{")
                print("\t//"+b['description'] + "\n\n\
    List<Player> targets = Map.visibles(pl, map);\n\
    Player chosen = SInteraction.chooseTarget(pl.getConn(), targets);\n\
    chosen.applyEffects(EffectsLambda.damage(, pl));\n\
    chosen.applyEffects(EffectsLambda.marks(, pl));\n\
});\n")