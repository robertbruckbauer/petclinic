import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Icon extends SvelteTypedComponent<IconProps, IconEvents, IconSlots> {
}

declare const _IconProps: {

    /** 
     * Disabled state. 
     */
    disabled?: boolean;

    /** 
     * Material icon name.
     */
    name?: string;

    /** 
     * Outlined variant.
     */
    outlined?: boolean;

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _IconEvents: {
    click: MouseEvent;
    mouseover: MouseEvent;
    focus: FocusEvent;
    blur: FocusEvent;
};

declare const _IconSlots: {
};

export declare type IconProps = typeof _IconProps;
export declare type IconEvents = typeof _IconEvents;
export declare type IconSlots = typeof _IconSlots;
export {
};
