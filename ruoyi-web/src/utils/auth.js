import Cookies from 'js-cookie'

const TokenKey = 'token'
const OwnerKey = 'owner'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getOwner() {
  const owner = localStorage.getItem(OwnerKey)
  return owner ? Number(owner) : null
}

export function setOwner(owner) {
  if (owner !== null && owner !== undefined) {
    localStorage.setItem(OwnerKey, owner.toString())
  } else {
    removeOwner()
  }
}

export function removeOwner() {
  localStorage.removeItem(OwnerKey)
}
