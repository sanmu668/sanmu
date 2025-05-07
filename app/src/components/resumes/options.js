import { computed, ref } from 'vue'

// Called by templates to decrease redundancy
export function getVueOptions(name) {
    return {
        name,
        props: {
            person: {
                type: Object,
                required: true
            },
            lang: {
                type: Object,
                required: true
            }
        },
        setup(props) {
            const person = ref(props.person)
            const contactLinks = useContactLinks(person)

            return {
                contactLinks
            }
        }
    };
}

// 生成联系方式链接
export function useContactLinks(person) {
    return computed(() => {
        const links = {}
        const contact = person.contact || {}

        if (contact.github) {
            links.github = `https://github.com/${contact.github}`
        }

        if (contact.codefights) {
            links.codefights = `https://codefights.com/profile/${contact.codefights}`
        }

        if (contact.medium) {
            links.medium = `https://medium.com/@${contact.medium}`
        }

        if (contact.email) {
            links.email = `mailto:${contact.email}`
        }

        if (contact.linkedin) {
            links.linkedin = `https://linkedin.com/in/${contact.linkedin}`
        }

        if (contact.phone) {
            links.phone = `tel:${contact.phone}`
        }

        return links
    })
}

// 生成语言包
export function useLang(terms, lang = 'zh') {
    return computed(() => {
        const defaultLang = terms.zh
        const useLang = terms[lang] || defaultLang

        // 使用默认语言填充未设置的字段
        Object.keys(defaultLang)
            .filter(k => !useLang[k])
            .forEach(k => {
                useLang[k] = defaultLang[k]
            })

        return useLang
    })
}
