import defaultSettings from '@/settings'

const title = defaultSettings.title || '无界供应链'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
