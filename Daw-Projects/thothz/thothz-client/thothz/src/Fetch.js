import fetch from 'isomorphic-fetch'

function fetching(href, headers){
    return fetch(href, headers)
      .then(resp => {
        if(resp.ok){
          return resp.json().then(body => ({resp, body}))
        }
        alert('Error!!')
        return {resp}
      })
}
export default fetching