const toSingle = (data) => {
    let href
    const links = data.links === undefined ? [] : data.links.map(link => {
        if(link.rel[0] === 'self')
            href = link.href
        return {href: link.href, rel: link.rel[0]}
    })

    const properties = {}
    for(let name in data.properties){
        if(name !== '$siren4j.class')
            properties[name] = data.properties[name]
    }

    const ent = data.entities === undefined ? [] : data.entities.map(e => {
        return {
            type: e.class[0],
            relation: e.rel[0],
            href: e.href
        }
    })

    const actions = data.actions === undefined ? [] : data.actions.map(a => a)

    const type = data.class[0][0].toUpperCase() + data.class[0].slice(1)
    return new Single(type, href, links, properties, ent, actions)
}

function Single(type, href, links, properties, entities, actions){
    this.type = type
    this.href = href
    this.links = links
    this.properties = properties
    this.entities = entities
    this.actions = actions
}

export default toSingle