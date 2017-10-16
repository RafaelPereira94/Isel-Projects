import toSingle from './Single.js'

export default (data) => {
    let href
    const singles = data.entities === undefined ? [] : data.entities.map(toSingle)
    const links = data.links === undefined ? [] : data.links.map(link => {
        if(link.rel[0] === 'self')
            href = link.href
        return {href: link.href, rel: link.rel[0]}
    })
    const actions = data.actions === undefined ? [] : data.actions.map(a => a)
    const type = data.class[0][0].toUpperCase() + data.class[0].slice(1)
    return new Collection(type, href, singles, links, data.properties, actions)
}

function Collection(type, href, singles, links, properties, actions){
    this.type = type
    this.href = href
    this.items = singles
    this.links = links
    this.pagination = properties
    this.actions = actions
}