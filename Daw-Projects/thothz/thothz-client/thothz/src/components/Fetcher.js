import React from 'react'
import fetch from 'isomorphic-fetch'

export default class Fetcher extends React.Component {
  constructor (props) {
    super(props)
    this.state = {loading: true}
  }

  handleFetch (url) {
    this.setState({loading: true})
    const headers = new Headers()
    headers.append('Accept', 'application/vnd.siren+json, application/json')
    fetch(url, {headers})
      .then(resp => {
        return resp.ok ? resp.json().then(body => ({resp: resp, body: body})) : {resp: resp}
      })
      .then(({resp, body}) => {
          if(this.props.transform === undefined)
            this.setState({loading: false, resp, body})
          else
            this.setState({loading: false, resp, body: this.props.transform(body)})
      })
  }

  componentDidMount () {
    this.handleFetch(this.props.url)
  }

  render () {
    return this.props.render(this.state.loading, this.state.resp, this.state.body, this.handleFetch.bind(this))
  }
}