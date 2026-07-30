import { marked } from 'marked'
import DOMPurify from 'dompurify'

marked.setOptions({ breaks: true, gfm: true })

export function renderMarkdown(rawContent) {
  const html = marked.parse(rawContent || '')
  return DOMPurify.sanitize(html)
}
