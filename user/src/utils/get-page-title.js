import defaultSettings from '@/settings'

const title = defaultSettings.title || '無界零售'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
