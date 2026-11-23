import { getHomeCategories } from '@/api/home'
import { ref } from 'vue'

const FALLBACK = [
  { id: 1, code: 'electronics', name: '电子产品', description: '', aliases: ['电子产品', '数码', '电子设备'] },
  { id: 2, code: 'cards', name: '证件卡类', description: '', aliases: ['证件卡类', '证件类', '校园卡', '身份证', '学生证', '银行卡'] },
  { id: 3, code: 'books', name: '书籍资料', description: '', aliases: ['书籍资料', '书籍类', '教材', '图书'] },
  { id: 4, code: 'clothing', name: '衣物饰品', description: '', aliases: ['衣物饰品', '服饰配件', '钥匙', '眼镜', '雨伞'] },
  { id: 5, code: 'study', name: '学习用品', description: '', aliases: ['学习用品', '文具', '学习类'] },
  { id: 6, code: 'others', name: '其他物品', description: '', aliases: ['其他物品', '其他', '生活用品', '钱包', '卡包'] }
]

const normalizeText = (value) => String(value || '').trim()

const parseAliases = (item) => {
  const raw = item?.aliases ?? item?.alias ?? item?.tags ?? item?.keywords ?? []
  if (Array.isArray(raw)) {
    return raw.map(normalizeText).filter(Boolean)
  }
  if (typeof raw === 'string') {
    return raw
      .split(/[,，、|/]/)
      .map(normalizeText)
      .filter(Boolean)
  }
  return []
}

/**
 * 加载与后台一致的物品分类（code 写入数据库/筛选；name 用于展示）
 * 优先使用后端分类，fallback 仅在接口失败或无数据时兜底
 */
export function useItemCategories() {
  const categories = ref([])
  const loading = ref(false)

  const loadCategories = async () => {
    loading.value = true
    try {
      const res = await getHomeCategories()
      const raw = res?.code === 200 ? res.data : null

      if (Array.isArray(raw) && raw.length > 0) {
        categories.value = raw.map((c, idx) => {
          const code = normalizeText(c.code || c.name || `cat_${idx + 1}`)
          const name = normalizeText(c.name || c.code || '未命名')
          const description = normalizeText(c.description || '')
          const aliases = parseAliases(c)

          return {
            id: c.id ?? idx + 1,
            code,
            name,
            description,
            aliases
          }
        })
        return
      }
    } catch (e) {
      console.warn('加载分类失败，使用默认分类', e)
    } finally {
      loading.value = false
    }

    categories.value = [...FALLBACK]
  }

  const aliasMapForCode = (code) => {
    const target = normalizeText(code)
    const c = categories.value.find(
      (x) => normalizeText(x.code) === target || normalizeText(x.name) === target
    )

    if (!c) return [target].filter(Boolean)

    const set = new Set([
      normalizeText(c.code),
      normalizeText(c.name),
      normalizeText(c.description),
      ...(Array.isArray(c.aliases) ? c.aliases.map(normalizeText) : [])
    ].filter(Boolean))

    return Array.from(set)
  }

  const findCategoryByText = (text) => {
    const keyword = normalizeText(text)
    if (!keyword) return null

    return categories.value.find((c) => {
      const pool = [
        normalizeText(c.code),
        normalizeText(c.name),
        normalizeText(c.description),
        ...(Array.isArray(c.aliases) ? c.aliases.map(normalizeText) : [])
      ].filter(Boolean)

      return pool.some((item) => item === keyword || item.includes(keyword) || keyword.includes(item))
    }) || null
  }

  const getCategoryText = (codeOrName) => {
    const keyword = normalizeText(codeOrName)
    if (!keyword) return ''
    const hit = categories.value.find(
      (x) => normalizeText(x.code) === keyword || normalizeText(x.name) === keyword
    )
    return hit?.name || keyword
  }

  return {
    categories,
    loading,
    loadCategories,
    aliasMapForCode,
    findCategoryByText,
    getCategoryText,
    FALLBACK
  }
}