const CategoryEnum = {
    "CUIDAR_DE_ANIMAIS":"Animal Caring",
    "ENSINAR_IDIOMAS":"Language Teaching",
    "ENSINAR_MUSICA":"Music Teaching",
    "INICIATIVAS_AMBIENTAIS":"Environmental Initiatives",
    "DESASTRES_AMBIENTAIS":"Environmental Disasters",
    "COMUNICACAO_DIGITAL":"Digital Comunication",
    "AUXILIO_DE_DOENTES":"Aiding The Sick",
    "AJUDAR_PORTADORES_DE_DEFICIENCIA":"Aiding the Disabled",
    "AJUDA_DESPORTIVA":"Sports Aid",
    "AJUDA_EMPRESARIAL":"Business Aid",
    "AJUDA_A_CRIANCAS":"Aiding Children",
    "AJUDA_A_IDOSOS":"Aiding the Elderly",
    "AJUDA_A_SEM_ABRIGO":"Aiding the Homeless",
    "INTERNACIONAL":"Aiding the Foreign",
    "PROTECAO_CIVIL":"Civil Protection",
    "SOCIAL":"Social",
    "RECICLAGEM":"Recyling",
    "CONSTRUCAO":"Construction"
}

function getCategory(name) {
    let entries = Object.entries(CategoryEnum);
    for (i = 0; i < entries.length; i++) {
        if (entries[i][0] == name)
            return entries[i][1];
    }
    //console.log("No category as such");
    return "Invalid Category";
}

Object.freeze(CategoryEnum);